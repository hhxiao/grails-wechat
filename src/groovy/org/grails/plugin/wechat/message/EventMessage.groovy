package org.grails.plugin.wechat.message

/**
 * Created by hhxiao on 9/29/14.
 */
class EventMessage extends Message {
    EventType event
    String eventKey // 事件KEY值，qrscene_为前缀，后面为二维码的参数值
    String ticket // 二维码的ticket，可用来换取二维码图片

    String latitude // 地理位置纬度
    String longitude // 地理位置经度
    String precision // 地理位置精度

    String status   // 事件状态

    int totalCount
    int filterCount
    int sentCount
    int errorCount
}
