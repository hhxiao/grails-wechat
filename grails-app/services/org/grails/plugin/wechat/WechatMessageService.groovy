package org.grails.plugin.wechat

import org.grails.plugin.wechat.annotation.VerificationRequired
import org.grails.plugin.wechat.bean.MessageType
import org.grails.plugin.wechat.util.HttpUtils

/**
 * Created by haihxiao on 2014/12/12.
 */
@VerificationRequired
class WechatMessageService {
    private static final String MASS_MESSAGE_URL = "https://api.weixin.qq.com/cgi-bin/message/mass/sendall?access_token="

    def wechatTokenService

    def sendNews(String mediaId, def to = '') {
        sendMessage(to, MessageType.MPNEWS, mediaId)
    }

    def sendText(String content, def to = '') {
        sendMessage(to, MessageType.TEXT, content)
    }

    def sendVoice(String mediaId, def to = '') {
        sendMessage(to, MessageType.VOICE, mediaId)
    }

    def sendImage(String mediaId, def to = '') {
        sendMessage(to, MessageType.IMAGE, mediaId)
    }

    def sendVideo(String mediaId, def to = '') {
        sendMessage(to, MessageType.VIDEO, mediaId)
    }

    private def sendMessage(def to, MessageType type, String content) {
        def msg = ['msgtype': type.title]
        msg.put(type.title, type.getContent(content))
        if(to instanceof String) {
            msg.put('filter', ['is_to_all': false, 'group_id': to])
        } else if(to instanceof Collection) {
            msg.put('touser', to)
        } else {
            msg.put('filter', ['is_to_all': true])
        }
        String url = MASS_MESSAGE_URL + wechatTokenService.accessToken
        HttpUtils.postJson(url, msg)
    }
}
