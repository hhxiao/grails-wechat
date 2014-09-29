package org.grails.plugin.wechat

import org.grails.plugin.wechat.message.Message
import org.springframework.beans.factory.InitializingBean

/**
 * Created by haihxiao on 17/9/14.
 */
class WechatService {
    private static final String CUSTOM_SERVICE_URL = "https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=ACCESS_TOKEN"

    def wechatConfigService

    void sendCustomMessage(Message message) {

    }
}
