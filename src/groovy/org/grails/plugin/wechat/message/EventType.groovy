package org.grails.plugin.wechat.message

/**
 * Created by hhxiao on 9/29/14.
 */
public enum EventType {
    UNKNOWN,        // 未知事件

    SUBSCRIBE,      // 关注
    UNSUBSCRIBE,    // 取消关注

    SCAN,       // 扫描带场景值二维码
    LOCATION,   // 地理位置事件

    CLICK,  // 点击菜单拉取消息时的事件推送
    VIEW,   // 点击菜单跳转链接时的事件推送

    SCANCODE_PUSH,      // 扫码推事件
    SCANCODE_WAITMSG,   // 扫码推事件且弹出“消息接收中”提示框的事件推送

    PIC_SYSPHOTO,       // 弹出系统拍照发图的事件推送
    PIC_PHOTO_OR_ALBUM, // 弹出拍照或者相册发图的事件推送
    PIC_WEIXIN,         // 弹出微信相册发图器的事件推送
    LOCATION_SELECT,    // 弹出地理位置选择器的事件推送

    MASSSENDJOBFINISH,      // 消息群发结束
    TEMPLATESENDJOBFINISH;  // 模板消息发送结束

    static EventType of(String value) {
        try {
            valueOf(value.toUpperCase())
        } catch(IllegalArgumentException e) {
            UNKNOWN
        }
    }
}
