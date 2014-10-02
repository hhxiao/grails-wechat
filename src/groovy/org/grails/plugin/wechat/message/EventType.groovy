package org.grails.plugin.wechat.message

/**
 * Created by hhxiao on 9/29/14.
 */
public enum EventType {
    subscribe, // 关注
    unsubscribe, // 取消关注

    SCAN,  // 扫描带场景值二维码
    LOCATION, // 地理位置事件

    CLICK, // 自定义菜单点击事件
    VIEW, // 点击菜单跳转链接时的事件

    TEMPLATESENDJOBFINISH // 模板消息发送结束
}
