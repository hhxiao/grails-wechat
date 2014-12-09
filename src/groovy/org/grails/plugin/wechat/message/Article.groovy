package org.grails.plugin.wechat.message

/**
 * Created by hhxiao on 9/29/14.
 */
class Article {
    String title //图文消息标题
    String description	// 图文消息描述
    String picUrl	// 图片链接，支持JPG、PNG格式，较好的效果为大图360*200，小图200*200
    String url	// 点击图文消息跳转链接

    String toXml() {
        """  <item>
   <Title><![CDATA[${title?:''}]]></Title>
   <Description><![CDATA[${description?:''}]]></Description>
   <PicUrl><![CDATA[${picUrl?:''}]]></PicUrl>
   <Url><![CDATA[${url?:''}]]></Url>
  </item>
"""
    }

    Map<String, Object> toJson() {
        [title: title?:'', description: description?:'', url: url?:'', picurl: picUrl?:'']
    }
}
