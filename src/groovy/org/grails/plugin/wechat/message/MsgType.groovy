package org.grails.plugin.wechat.message

/**
 * Created by hhxiao on 9/29/14.
 */
public enum MsgType {
    text,    // 文本消息
    image,   // 图片消息
    voice,   // 语音消息
    video,   // 视频消息
    music,   // 音乐消息
    news,    // 图文消息
    location,    // 地理位置消息
    link,    // 链接消息
    event;    // 事件消息
}
