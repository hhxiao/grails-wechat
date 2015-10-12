package org.grails.plugin.wechat

import org.codehaus.groovy.grails.web.mapping.LinkGenerator
import org.grails.plugin.wechat.bean.PayData
import org.grails.plugin.wechat.bean.PayDataKey
import org.grails.plugin.wechat.bean.TradeType
import org.grails.plugin.wechat.util.HttpUtils
import org.grails.plugin.wechat.util.SignatureHelper
import org.grails.plugin.wechat.util.StringUtils
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired

import java.text.SimpleDateFormat
/**
 * Authors: haihxiao
 * Date: 15/7/30
 **/
class WechatPayService implements InitializingBean {
    static final String UNIFIED_ORDER_API = 'https://api.mch.weixin.qq.com/pay/unifiedorder'
    static final String QUERY_ORDER_API = 'https://api.mch.weixin.qq.com/pay/orderquery'
    static final String CLOSE_ORDER_API = 'https://api.mch.weixin.qq.com/pay/closeorder'
    static final String REFUND_API = 'https://api.mch.weixin.qq.com/secapi/pay/refund'
    static final String QUERY_REFUND_API = 'https://api.mch.weixin.qq.com/secapi/pay/refundquery'
    static final String DOWNLOAD_BILL_API = "https://api.mch.weixin.qq.com/pay/downloadbill"

    def wechatTokenService
    def grailsApplication
    @Autowired
    LinkGenerator linkGenerator

    String getQrCodeText(String productId) {
        def data = [appid: wechatTokenService.appId, mch_id: merchantId,
                    product_id: productId, nonce_str: StringUtils.randomString(32, true),
                    time_stamp: System.currentTimeMillis()]
        def sign = SignatureHelper.sign(data)
        "weixin://wxpay/bizpayurl?sign=${sign}&appid=${data.appid}&mch_id=${data.mch_id}&product_id=${data.product_id}&time_stamp=${data.time_stamp}&nonce_str=${data.time_stamp}"
    }

    PayData unifiedOrderForProduct(String out_trade_no, String product_id, int total_fee, String title, String attach = '') {
        PayData payData = new PayData()

        payData.put(PayDataKey.product_id, product_id)
        payData.put(PayDataKey.body, title)
        payData.put(PayDataKey.attach, attach)
        payData.put(PayDataKey.out_trade_no, out_trade_no)
        payData.put(PayDataKey.total_fee, total_fee)
        payData.put(PayDataKey.trade_type, TradeType.NATIVE)

        unifiedOrder(payData)
    }

    PayData unifiedOrderForOpenId(String out_trade_no, String openid, int total_fee, String title, String attach = '') {
        PayData payData = new PayData()

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
        payData.put(PayDataKey.mch_id, merchantId)
        payData.put(PayDataKey.nonce_str, StringUtils.randomString(32, true))

        String callbackUrl = linkGenerator.link(controller: 'wxpay', action: 'result', absolute: true)
        payData.putIfAbsent(PayDataKey.notify_url, callbackUrl)
        payData.putIfAbsent(PayDataKey.spbill_create_ip, paymentIp)

        payData.sign(paymentKey)
        payData.validate()
        String result = HttpUtils.post(UNIFIED_ORDER_API, "application/xml", payData.toXml())
        PayData.fromXml(result)
    }

    def queryOrderForTransactionId(String transactionId) {
        PayData payData = new PayData()
        payData.put(PayDataKey.transaction_id, transactionId)
        queryOrder(payData)
    }

    def queryOrderForTradeNo(String tradeNo) {
        PayData payData = new PayData()
        payData.put(PayDataKey.out_trade_no, tradeNo)
        queryOrder(payData)
    }

    PayData queryOrder(PayData payData) {
        payData.put(PayDataKey.appid, wechatTokenService.appId)
        payData.put(PayDataKey.mch_id, merchantId)
        payData.put(PayDataKey.nonce_str, StringUtils.randomString(32, true))

        payData.sign(paymentKey)
        payData.validate()
        String result = HttpUtils.post(QUERY_ORDER_API, "application/xml", payData.toXml())
        PayData.fromXml(result)
    }

    PayData closeOrder(String tradeNo) {
        PayData payData = new PayData()
        payData.put(PayDataKey.out_trade_no, tradeNo)
        payData.put(PayDataKey.appid, wechatTokenService.appId)
        payData.put(PayDataKey.mch_id, merchantId)
        payData.put(PayDataKey.nonce_str, StringUtils.randomString(32, true))
        payData.sign(paymentKey)
        payData.validate()
        String result = HttpUtils.post(CLOSE_ORDER_API, "application/xml", payData.toXml())
        PayData.fromXml(result)
    }

    PayData refundForTransactionId(String transactionId, String outRefundNo, int totalFee) {
        PayData payData = new PayData()
        payData.put(PayDataKey.transaction_id, transactionId)
        refund(payData, outRefundNo, totalFee)
    }

    PayData refundForTradeNo(String tradeNo, String outRefundNo, int totalFee) {
        PayData payData = new PayData()
        payData.put(PayDataKey.out_trade_no, tradeNo)
        refund(payData, outRefundNo, totalFee)
    }

    PayData refund(PayData payData, String outRefundNo, int totalFee) {
        payData.put(PayDataKey.total_fee, totalFee)
        payData.put(PayDataKey.refund_fee, totalFee)
        payData.put(PayDataKey.out_refund_no, outRefundNo)

        payData.put(PayDataKey.appid, wechatTokenService.appId)
        payData.put(PayDataKey.mch_id, merchantId)
        payData.put(PayDataKey.op_user_id, merchantId)
        payData.put(PayDataKey.nonce_str, StringUtils.randomString(32, true))
        payData.sign(paymentKey)
        payData.validate()
        String result = HttpUtils.post(REFUND_API, "application/xml", payData.toXml())
        PayData.fromXml(result)
    }

    PayData queryRefundForTransactionId(String transactionId) {
        PayData payData = new PayData()
        payData.put(PayDataKey.transaction_id, transactionId)
        queryRefund(payData)
    }

    PayData queryRefundForTradeNo(String tradeNo) {
        PayData payData = new PayData()
        payData.put(PayDataKey.out_trade_no, tradeNo)
        queryRefund(payData)
    }

    PayData queryRefundForRefundNo(String refundNo) {
        PayData payData = new PayData()
        payData.put(PayDataKey.out_refund_no, refundNo)
        queryRefund(payData)
    }

    PayData queryRefundForRefundId(String refundId) {
        PayData payData = new PayData()
        payData.put(PayDataKey.refund_id, refundId)
        queryRefund(payData)
    }

    PayData queryRefund(PayData payData) {
        payData.put(PayDataKey.appid, wechatTokenService.appId)
        payData.put(PayDataKey.mch_id, merchantId)
        payData.put(PayDataKey.nonce_str, StringUtils.randomString(32, true))
        payData.sign(paymentKey)
        payData.validate()
        String result = HttpUtils.post(QUERY_REFUND_API, "application/xml", payData.toXml())
        PayData.fromXml(result)
    }

    String downloadBill(Date date) {
        PayData payData = new PayData()
        payData.put(PayDataKey.appid, wechatTokenService.appId)
        payData.put(PayDataKey.mch_id, merchantId)
        payData.put(PayDataKey.nonce_str, StringUtils.randomString(32, true))
        payData.put(PayDataKey.mch_id, merchantId)
        payData.put(PayDataKey.bill_date, new SimpleDateFormat("yyyyMMdd").format(date))
        payData.sign(paymentKey)
        payData.validate()
        HttpUtils.post(DOWNLOAD_BILL_API, "application/xml", payData.toXml())
    }

    private String _payMchId
    private String _payKey
    private String _payIp

    String getMerchantId() {
        _payMchId
    }

    String getPaymentKey() {
        _payKey
    }

    String getPaymentIp() {
        _payIp ?: InetAddress.getLocalHost().hostAddress
    }

    @Override
    void afterPropertiesSet() throws Exception {
        _payMchId = grailsApplication.config.grails?.wechat.pay?.mch?.toString()
        _payKey = grailsApplication.config.grails?.wechat.pay?.key?.toString()
        _payIp = grailsApplication.config.grails?.wechat.pay?.ip?.toString()

        if(!_payMchId || !_payKey) {
            throw new IllegalStateException("Weixin MerchantId or PaymentKey is not configured")
        }
    }
}
