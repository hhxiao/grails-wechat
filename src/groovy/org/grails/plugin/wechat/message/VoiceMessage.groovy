package org.grails.plugin.wechat.message

/**
 * Created by hhxiao on 9/29/14.
 */
class VoiceMessage extends Message implements ResponseMessage {
    VoiceMessage() {
        msgType = MsgType.voice
    }

    String mediaId // 语音消息媒体id，可以调用多媒体文件下载接口拉取数据。
    String format // 语音格式，如amr，speex等
    String recognition // 语音识别结果，UTF8编码

    String getAdditionalResponseXml() {
        "<Voice><MediaId><![CDATA[${mediaId}]]></MediaId></Voice>"
    }

    Map<String, Object> getAdditionalResponseJson() {
        ['media_id': mediaId]
    }
}
