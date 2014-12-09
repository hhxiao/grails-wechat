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

    String status   // 发送状态

    int totalCount      // group_id下粉丝数；或者openid_list中的粉丝数
    int filterCount     // 过滤（过滤是指特定地区、性别的过滤、用户设置拒收的过滤，用户接收已超4条的过滤）后，准备发送的粉丝数，原则上，FilterCount = SentCount + ErrorCount
    int sentCount       // 发送成功的粉丝数
    int errorCount      // 发送失败的粉丝数

    String scanCodeInfo // 扫描信息
    String scanType     // 扫描类型，一般是qrcode
    String scanResult   // 扫描结果，即二维码对应的字符串信息

    @Override
    String toString() {
        "${super.toString()}:${event}:${eventKey}"
    }
}
