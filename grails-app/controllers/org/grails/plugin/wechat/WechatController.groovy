package org.grails.plugin.wechat
import grails.util.Environment
import org.grails.plugin.wechat.message.Message
import org.grails.plugin.wechat.util.MessageUtils
/**
 * Created by haihxiao on 17/9/14.
 */
class WechatController {
    private static boolean productionEnv = Environment.getCurrent() == Environment.PRODUCTION

    static allowedMethods = [echo: "GET", post: "POST"]

    def wechatHandlerService
    def wechatTokenService
    def securityHelper

    static beforeInterceptor = {
        String error = null
        if(!params.signature) error = "signature is required"
        else if(!params.timestamp) error = "timestamp is required"
        else if(!params.nonce) error = "nonce is required"
        if(error) {
            response.sendError(HttpURLConnection.HTTP_BAD_REQUEST, error)
            return false
        }
        boolean valid = wechatTokenService.checkSignature(params.signature, params.timestamp, params.nonce)
        if(!valid) {
            if(productionEnv) {
                render '[SigErr]'
                return false
            } else {
                log.warn("Invalid Signature")
            }
        }
        return true
    }

    def echo(String echostr) {
        render echostr
    }

    def post() {
        Message message = MessageUtils.fromGPathResult(request.XML)
        Object ret = securityHelper ? securityHelper.authenticate(message.fromUserName) : null
        if(log.debugEnabled) {
            if(ret) {
                log.debug("${ret} - ${message.toString()}")
            } else {
                log.debug(message.toString())
            }
        }
        println message
        try {
            render wechatHandlerService.handleMessage(message)
        } finally {
            if(securityHelper) securityHelper.reset()
        }
    }
}
