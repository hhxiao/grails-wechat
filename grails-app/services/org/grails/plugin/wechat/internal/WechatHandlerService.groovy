package org.grails.plugin.wechat.internal

import org.grails.plugin.wechat.message.Message
import org.grails.plugin.wechat.message.ResponseMessage
import org.grails.plugin.wechat.util.HandlersRegistry
import org.grails.plugin.wechat.util.MessageUtils

/**
 * Created by haihxiao on 2014/10/1.
 */
class WechatHandlerService {
    def wechatHandlersRegistry

    String handleMessage(Message message) {
        String fromUserName = message.fromUserName
        String toUserName = message.toUserName

        List<HandlersRegistry.Handler> messageHandlers = wechatHandlersRegistry.getMessageHandlers(message)

        Object result = null
        messageHandlers.each {
            Object res = it.process(message)
            if(res != null) result = res
        }
        if(result instanceof ResponseMessage) {
            ((Message)result).fromUserName = toUserName
            ((Message)result).toUserName = fromUserName
            return MessageUtils.toXml(result)
        }
        return ''
    }
}
