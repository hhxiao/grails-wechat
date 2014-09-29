package org.grails.plugin.wechat.message

/**
 * Created by hhxiao on 9/29/14.
 */
class ImageMessage extends Message {
    String picUrl   // 图片链接
    String mediaId // 图片消息媒体id，可以调用多媒体文件下载接口拉取数据。

    String getAdditionalResponseXml() {
        "<Image><MediaId><![CDATA[${mediaId}]]></MediaId></Image>"
    }
}
