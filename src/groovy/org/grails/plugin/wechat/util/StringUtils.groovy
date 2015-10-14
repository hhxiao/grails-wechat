package org.grails.plugin.wechat.util

import groovy.xml.XmlUtil

/**
 * Authors: haihxiao
 * Date: 15/7/31
 **/
class StringUtils {
    static String randomString(int length, boolean capitalize = false) {
        if (length < 1) {
            return ''
        }
        // Create a char buffer to put random letters and numbers in.
        char[] randBuffer = new char[length]
        for (int i = 0; i < randBuffer.length; i++) {
            randBuffer[i] = numbersAndLetters[randGen.nextInt(numbersAndLetters.length)]
        }
        return capitalize ? new String(randBuffer).toUpperCase() : new String(randBuffer).toLowerCase()
    }

    static String toXmlString(def xml) {
        return XmlUtil.serialize(xml).replaceAll("[\n]", "").replaceAll(">\\s+?<", "><").trim()
    }

    private static final Random randGen = new Random();
    private static final char[] numbersAndLetters = ("0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ").toCharArray()
}
