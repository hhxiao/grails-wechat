package org.grails.plugin.wechat

import org.grails.plugin.wechat.bean.AccessToken
import org.grails.plugin.wechat.message.ResponseMessage
import org.grails.plugin.wechat.util.HttpUtils
import org.grails.plugin.wechat.util.MessageUtils
/**
 * 微信客服接口
 *
 * Created by haihxiao on 2014/9/29.
 */
class WechatCustomerService {
    private static final String CUSTOM_SERVICE_URL = "https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=ACCESS_TOKEN"

    def wechatTokenService

    String sendMessage(ResponseMessage message) {
        AccessToken token = wechatTokenService.accessToken
        String url = CUSTOM_SERVICE_URL.replace('ACCESS_TOKEN', token.accessToken)
        HttpUtils.postJson(url, MessageUtils.toJson(message))
    }
}
