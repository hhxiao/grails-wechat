package org.grails.plugin.wechat

import org.grails.plugin.wechat.bean.PayData
import org.grails.plugin.wechat.bean.PayDataKey
import org.grails.plugin.wechat.bean.TradeType
import org.grails.plugin.wechat.util.HttpUtils
import org.grails.plugin.wechat.util.SignatureHelper
import org.grails.plugin.wechat.util.StringUtils

/**
 * Authors: haihxiao
 * Date: 15/7/30
 **/
class WechatPayService {
    static final String UNIFIED_ORDER_API = 'https://api.mch.weixin.qq.com/pay/unifiedorder'

    def wechatTokenService

    String getQrCodeText(String productId) {
        def data = [appid: wechatTokenService.appId, mch_id: wechatTokenService.merchantId,
                    product_id: productId, nonce_str: StringUtils.randomString(32, true),
                    time_stamp: System.currentTimeMillis()]
        def sign = SignatureHelper.sign(data)
        "weixin//wxpay/bizpayurl?sign=${sign}&appid=${data.appid}&mch_id=${data.mch_id}&product_id=${data.product_id}&time_stamp=${data.time_stamp}&nonce_str=${data.time_stamp}"
    }

    PayData unifiedOrderForProduct(String out_trade_no, String product_id, int total_fee, String title, String attach = '') {
        def payData = new PayData()

        payData.put(PayDataKey.product_id, product_id)
        payData.put(PayDataKey.body, title)
        payData.put(PayDataKey.attach, attach)
        payData.put(PayDataKey.out_trade_no, out_trade_no)
        payData.put(PayDataKey.total_fee, total_fee)
        payData.put(PayDataKey.trade_type, TradeType.NATIVE)

        unifiedOrder(payData)
    }

    PayData unifiedOrderForOpenId(String out_trade_no, String openid, int total_fee, String title, String attach = '') {
        def payData = new PayData()

        payData.put(PayDataKey.openid, openid)
        payData.put(PayDataKey.body, title)
        payData.put(PayDataKey.attach, attach)
        payData.put(PayDataKey.out_trade_no, out_trade_no)
        payData.put(PayDataKey.total_fee, total_fee)
        payData.put(PayDataKey.trade_type, TradeType.JSAPI)

        unifiedOrder(payData)
    }

    PayData unifiedOrder(PayData payData) {
        payData.put(PayDataKey.appid, wechatTokenService.appId)
        payData.put(PayDataKey.mch_id, wechatTokenService.merchantId)
        payData.put(PayDataKey.nonce_str, StringUtils.randomString(32, true))

        payData.putIfAbsent(PayDataKey.notify_url, wechatTokenService.paymentCallback)
        payData.putIfAbsent(PayDataKey.spbill_create_ip, wechatTokenService.paymentIp)

        payData.sign(wechatTokenService.paymentKey)
        payData.validate()
        String result = HttpUtils.post(UNIFIED_ORDER_API, "application/xml", payData.toXml())
        PayData.fromXml(result)
    }
}
