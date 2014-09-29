package org.grails.plugin.wechat.message

/**
 * Created by hhxiao on 9/29/14.
 */
class LocationMessage extends Message {
    String location_X // 地理位置维度
    String location_Y // 地理位置经度
    String scale // 地图缩放大小
    String label // 地理位置信息
}
