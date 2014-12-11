package org.grails.plugin.wechat.message

/**
 * Created by haihxiao on 2014/12/11.
 */
class ScanCodeInfo {
    String scanType     // 扫描类型，一般是qrcode
    String scanResult   // 扫描结果，即二维码对应的字符串信息

    String toString() {
        "$scanType:$scanResult"
    }
}
