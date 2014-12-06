package org.grails.plugin.wechat
import grails.util.Environment
import groovy.xml.XmlUtil
import org.grails.plugin.wechat.message.Message
import org.grails.plugin.wechat.util.MessageUtils
/**
 * Created by haihxiao on 17/9/14.
 */
class WechatController {
    static allowedMethods = [echo: "GET", post: "POST"]

    def wechatHandlerService
    def wechatTokenService

    private static boolean productionEnv = Environment.getCurrent() == Environment.PRODUCTION

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
        if(log.debugEnabled) {
            log.debug(XmlUtil.serialize(request.XML))
        }
        Message message = MessageUtils.fromGPathResult(request.XML)
        render wechatHandlerService.handleMessage(message)
    }
}
