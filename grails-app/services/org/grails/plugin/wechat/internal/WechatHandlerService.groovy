package org.grails.plugin.wechat.internal

import org.grails.plugin.wechat.message.Message
import org.grails.plugin.wechat.message.ResponseMessage
import org.grails.plugin.wechat.util.HandlersRegistry
import org.grails.plugin.wechat.util.MessageUtils

/**
 * Internal service, supposed only called by WechatController
 * 
 * Created by haihxiao on 2014/10/1.
 */
class WechatHandlerService {
    def wechatHandlersRegistry

    String handleMessage(Message message) {
        String fromUserName = message.fromUserName
        String toUserName = message.toUserName

        Collection<HandlersRegistry.Handler> messageHandlers = wechatHandlersRegistry.getMessageHandlers(message)

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
}
