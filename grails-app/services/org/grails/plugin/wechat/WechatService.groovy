package org.grails.plugin.wechat

import org.grails.plugin.wechat.message.Message
import org.grails.plugin.wechat.message.ResponseMessage
import org.grails.plugin.wechat.token.AccessToken
import org.grails.plugin.wechat.util.HttpUtils
import org.grails.plugin.wechat.util.JsonHelper
import org.grails.plugin.wechat.util.MessageUtils
import org.springframework.beans.factory.InitializingBean

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * 微信基本接口
 *
 * Created by haihxiao on 17/9/14.
 */
class WechatService {
    void sendReply(HttpServletResponse response, ResponseMessage reply) {
        response.setContentType('application/xml')
        response.sendError(HttpURLConnection.HTTP_OK, MessageUtils.toXml(reply))
    }
}
