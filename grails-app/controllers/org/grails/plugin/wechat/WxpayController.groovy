package org.grails.plugin.wechat
/**
 * Authors: Hai-Hua Xiao (hhxiao@gmail.com)
 * Date: 15/10/12
 **/
class WxpayController {
    def wechatHandlerService

    static allowedMethods = [echo: "GET", callback: "POST", result: "POST"]

    def echo() {
        render "微信扫码支付回调接口"
    }

    def callback(String openid, String productid) {
        render wechatHandlerService.handlePayment(openid, productid)
    }

    def result() {
        render(text: "<xml><return_code>SUCCESS</return_code><return_msg>OK</return_msg></xml>", contentType: "text/xml", encoding: "UTF-8")
    }
}
