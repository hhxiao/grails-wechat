package org.grails.plugin.wechat.message

/**
 * Created by hhxiao on 9/29/14.
 */
class VideoMessage extends Message implements ResponseMessage {
    String mediaId // 视频消息媒体id，可以调用多媒体文件下载接口拉取数据。
    String thumbMediaId // 视频消息缩略图的媒体id，可以调用多媒体文件下载接口拉取数据。

    String title
    String description

    String getAdditionalResponseXml() {
        """<Video>
<MediaId><![CDATA[${mediaId?:''}]]></MediaId>
<Title><![CDATA[${title?:''}]]></Title>
<Description><![CDATA[${description?:''}]]></Description>
</Video>"""
    }

    Map<String, Object> getAdditionalResponseJson() {
        ['media_id': mediaId?:'', 'thumb_media_id': thumbMediaId?:'', 'title': title?:'', 'description': description?:'']
    }

    @Override
    String toString() {
        "${super.toString()}:${mediaId}"
    }
}
