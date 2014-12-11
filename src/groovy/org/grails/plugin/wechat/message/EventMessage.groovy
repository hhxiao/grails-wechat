package org.grails.plugin.wechat.message

/**
 * Created by hhxiao on 9/29/14.
 */
class EventMessage extends Message {
    EventType event
    String status   // 发送状态
    String eventKey // 事件KEY值，qrscene_为前缀，后面为二维码的参数值
    String ticket // 二维码的ticket，可用来换取二维码图片

    // EventType.LOCATION
    String latitude // 地理位置纬度
    String longitude // 地理位置经度
    String precision // 地理位置精度

    int totalCount      // group_id下粉丝数；或者openid_list中的粉丝数
    int filterCount     // 过滤（过滤是指特定地区、性别的过滤、用户设置拒收的过滤，用户接收已超4条的过滤）后，准备发送的粉丝数，原则上，FilterCount = SentCount + ErrorCount
    int sentCount       // 发送成功的粉丝数
    int errorCount      // 发送失败的粉丝数

    // EventType.SCANCODE_PUSH
    // EventType.SCANCODE_WAITMSG
    ScanCodeInfo scanCodeInfo // 扫描信息

    // EventType.LOCATION_SELECT
    SendLocationInfo sendLocationInfo

    // EventType.PIC_WEIXIN
    // EventType.PIC_PHOTO_OR_ALBUM
    // EventType.PIC_SYSPHOTO
    SendPicsInfo sendPicsInfo

    @Override
    String toString() {
        String val = eventKey
        switch (event) {
            case EventType.SCANCODE_PUSH:
            case EventType.SCANCODE_WAITMSG: val = String.valueOf(scanCodeInfo); break
            case EventType.LOCATION_SELECT: val = String.valueOf(sendLocationInfo); break
            case EventType.LOCATION: val = "$latitude:$longitude:$precision"; break
            case EventType.PIC_WEIXIN:
            case EventType.PIC_PHOTO_OR_ALBUM:
            case EventType.PIC_SYSPHOTO: val = String.valueOf(sendPicsInfo); break
        }
        "${super.toString()}:${event}:${eventKey}:${val}"
    }
}
