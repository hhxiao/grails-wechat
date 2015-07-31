package org.grails.plugin.wechat

import org.grails.plugin.wechat.bean.AccessToken
import org.grails.plugin.wechat.util.SignatureHelper

/**
 * Cisco System
 * Authors: haihxiao
 * Date: 15/7/30
 **/
class WechatPayService {
    private static final String PAY_CODE_URL = "weixin//wxpay/bizpayurl?sign="

    def wechatTokenService

    String getQrCodeText(String prodId, String nonce) {
        String mchId = wechatTokenService.merchantId
        if(mchId == null) {
            throw new IllegalStateException("Weixin MechantId(grails.wechat.mch.id) is not configured")
        }
        def body = [appid: wechatTokenService.appId, mch_id: mchId, product_id: prodId, time_stamp: Long.toString(System.currentTimeMillis()), nonce_str: nonce]
        def sign = SignatureHelper.sign(body)
        "${PAY_CODE_URL}=${sign}&${body.entrySet().collect{"${it.key}=${it.value}"}.join('&')}"
    }
}
