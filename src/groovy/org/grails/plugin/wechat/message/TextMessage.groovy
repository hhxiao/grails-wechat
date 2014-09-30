package org.grails.plugin.wechat.message

/**
 * Created by hhxiao on 9/29/14.
 */
class TextMessage extends Message implements ResponseMessage {
    String content // 文本消息内容

    String getAdditionalResponseXml() {
        "<Content><![CDATA[${content}]]></Content>"
    }

    Map<String, Object> getAdditionalResponseJson() {
        ['content': content]
    }
}
