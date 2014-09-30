package org.grails.plugin.wechat

import org.grails.plugin.wechat.message.Message
import org.grails.plugin.wechat.message.ResponseMessage
import org.grails.plugin.wechat.util.MessageUtils

import javax.servlet.http.HttpServletResponse

/**
 * 微信基本接口
 *
 * Created by haihxiao on 17/9/14.
 */
class WechatService {
    String handleMessage(Message message) {
        MessageUtils.toXml(message) + "" + MessageUtils.toJson(message)
    }

    void sendReply(HttpServletResponse response, ResponseMessage reply) {
        response.setContentType('application/xml')
        response.sendError(HttpURLConnection.HTTP_OK, MessageUtils.toXml(reply))
    }
}
