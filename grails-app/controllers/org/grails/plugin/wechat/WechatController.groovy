package org.grails.plugin.wechat

import grails.util.Environment
import org.grails.plugin.wechat.message.Message
import org.grails.plugin.wechat.util.MessageUtils
import org.grails.plugin.wechat.util.SignatureHelper

/**
 * Created by haihxiao on 17/9/14.
 */
class WechatController {
    static allowedMethods = [check: "GET", post: "POST"]

    def wechatHandlerService
    def wechatTokenService

    static beforeInterceptor = {
        String error = null
        if(!params.signature) error = "signature is required"
        else if(!params.timestamp) error = "timestamp is required"
        else if(!params.nonce) error = "nonce is required"
        if(error) {
            response.sendError(HttpURLConnection.HTTP_BAD_REQUEST, error)
            return false
        }
        boolean valid = SignatureHelper.checkSignature(wechatTokenService.appToken, params.signature, params.timestamp, params.nonce)
        if(Environment.getCurrent() == Environment.PRODUCTION && !valid) {
            render '[SigErr]'
            return false
        }
        return true
    }

    def check(String echostr) {
        render echostr
    }

    def post() {
        Message message = MessageUtils.fromGPathResult(request.XML)
        render wechatHandlerService.handleMessage(message)
    }
}
