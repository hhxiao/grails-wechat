package org.grails.plugin.wechat.message
/**
 * Created by haihxiao on 2014/12/11.
 */
class SendLocationInfo {
    String location_X	// X坐标信息
    String location_Y	// Y坐标信息
    String scale        // 精度，可理解为精度或者比例尺、越精细的话 scale越高
    String label        // 地理位置的字符串信息
    String poiname      // 朋友圈POI的名字，可能为空

    String toString() {
        "$label:$location_X:$location_Y:$scale:$poiname"
    }
}
