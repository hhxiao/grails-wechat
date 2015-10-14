package org.grails.plugin.wechat

import grails.converters.JSON
import org.grails.plugin.wechat.bean.PayData
import org.grails.plugin.wechat.bean.TradeType
import org.grails.plugin.wechat.util.StringUtils
/**
 * Authors: Hai-Hua Xiao (hhxiao@gmail.com)
 * Date: 15/10/12
 **/
class WxpayController {
    def wechatHandlerService
    def wechatPayService

    static allowedMethods = [echo: "GET", callback: "POST", result: "POST"]

    def echo() {
        render "微信扫码支付回调接口"
    }

    def callback() {
        def xmlString = StringUtils.toXmlString(request.XML)
        log.info("callback: " + xmlString)
        PayData payData = PayData.fromXml(xmlString)
        payData = wechatHandlerService.handlePayment(payData)
        render(text: payData.toXml(), contentType: "text/xml", encoding: "UTF-8")
    }

    def result() {
        def xmlString = StringUtils.toXmlString(request.XML)
        log.info("result: " + xmlString)
        render(text: "<xml><return_code>SUCCESS</return_code><return_msg>OK</return_msg></xml>", contentType: "text/xml", encoding: "UTF-8")
    }

    def order(String openid, String trade, String product, int fee, String title) {
        def data = wechatPayService.unifiedOrderForJsApi(openid, trade, product, fee, title)
        render (data as JSON)
    }
}
