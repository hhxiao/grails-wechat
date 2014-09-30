package org.grails.plugin.wechat.message

/**
 * Created by hhxiao on 9/29/14.
 */
class MusicMessage extends Message implements ResponseMessage {
    String mediaId // 消息媒体id，可以调用多媒体文件下载接口拉取数据。
    String thumbMediaId

    String musicUrl
    String hqMusicUrl
    String title
    String description

    String getAdditionalResponseXml() {
        """<Music>
<Title><![CDATA[${title}]]></Title>
<Description><![CDATA[${description}]]></Description>
<MusicUrl><![CDATA[${musicUrl}]]></MusicUrl>
<HQMusicUrl><![CDATA[${hqMusicUrl}]]></HQMusicUrl>
<ThumbMediaId><![CDATA[${mediaId}]]></ThumbMediaId>
</Music>"""
    }

    Map<String, Object> getAdditionalResponseJson() {
        ['media_id': mediaId, 'thumb_media_id': thumbMediaId, 'title': title,
         'description': description, 'musicurl': musicUrl, 'hqmusicurl': hqMusicUrl]
    }
}
