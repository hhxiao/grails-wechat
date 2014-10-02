package org.grails.plugin.wechat.message

/**
 * Created by hhxiao on 9/29/14.
 */
public enum EventType {
    UNKNOWN,        // 未知事件

    subscribe,      // 关注
    unsubscribe,    // 取消关注

    SCAN,       // 扫描带场景值二维码
    LOCATION,   // 地理位置事件

    CLICK,  // 点击菜单拉取消息时的事件推送
    VIEW,   // 点击菜单跳转链接时的事件推送

    scancode_push,      // 扫码推事件
    scancode_waitmsg,   // 扫码推事件且弹出“消息接收中”提示框的事件推送

    pic_sysphoto,       // 弹出系统拍照发图的事件推送
    pic_photo_or_album, // 弹出拍照或者相册发图的事件推送
    pic_weixin,         // 弹出微信相册发图器的事件推送
    location_select,    // 弹出地理位置选择器的事件推送

    MASSSENDJOBFINISH,      // 消息群发结束
    TEMPLATESENDJOBFINISH;  // 模板消息发送结束

    static EventType of(String value) {
        try {
            valueOf(value)
        } catch(IllegalArgumentException e) {
            UNKNOWN
        }
    }
}
