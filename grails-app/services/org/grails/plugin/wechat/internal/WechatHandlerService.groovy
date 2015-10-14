package org.grails.plugin.wechat.internal

import org.grails.plugin.wechat.bean.PayData
import org.grails.plugin.wechat.bean.PayDataKey
import org.grails.plugin.wechat.message.Message
import org.grails.plugin.wechat.message.ResponseMessage
import org.grails.plugin.wechat.util.HandlersRegistry
import org.grails.plugin.wechat.util.MessageUtils
import org.grails.plugin.wechat.util.StringUtils

/**
 * Internal service, supposed only called by WechatController
 * 
 * Created by haihxiao on 2014/10/1.
 */
class WechatHandlerService {
    def wechatHandlersRegistry
    def wechatPayService
    def wechatTokenService

    String handleMessage(Message message) {
        String fromUserName = message.fromUserName
        String toUserName = message.toUserName

        Collection<HandlersRegistry.MHandler> messageHandlers = wechatHandlersRegistry.getMessageHandlers(message)

        ResponseMessage result = null
        messageHandlers.each {
            ResponseMessage res = it.process(message)
            if(res != null) result = res
        }
        if(result) {
            ((Message)result).fromUserName = toUserName
            ((Message)result).toUserName = fromUserName
            return MessageUtils.toXml(result)
        }
        return ''
    }

    PayData handlePayment(PayData payData) {
        Collection<HandlersRegistry.PHandler> paymentHandlers = wechatHandlersRegistry.getPaymentHandlers(payData)
        PayData newPayData = new PayData()
        newPayData.copy(payData, PayDataKey.openid, PayDataKey.product_id)
        newPayData.put(PayDataKey.appid, wechatTokenService.appId)
        newPayData.put(PayDataKey.mch_id, wechatPayService.merchantId)
        newPayData.put(PayDataKey.nonce_str, StringUtils.randomString(32, false))
        paymentHandlers.each {
            newPayData = it.process(newPayData)
        }
        newPayData.put(PayDataKey.return_code, "SUCCESS")
        if(newPayData.getString(PayDataKey.prepay_id)) {
            newPayData.put(PayDataKey.result_code, "SUCCESS")
        } else {
            newPayData.put(PayDataKey.result_code, "FAIL")
        }
        newPayData.sign(wechatPayService.paymentKey)
    }
}
