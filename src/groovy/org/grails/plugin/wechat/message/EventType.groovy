package org.grails.plugin.wechat.message

/**
 * Created by hhxiao on 9/29/14.
 */
public enum EventType {
    /**
     * 事件类型：subscribe(关注)
     */
    subscribe, // 关注
    unsubscribe, // 取消关注
    CLICK, // 自定义菜单点击事件
    SCAN,  // 扫描带场景值二维码
    LOCATION, // 地理位置事件
    VIEW; // 点击菜单跳转链接时的事件
}