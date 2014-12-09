package org.grails.plugin.wechat.message

/**
 * Created by hhxiao on 9/29/14.
 */
class LinkMessage extends Message {
    String title	// 消息标题
    String description	// 消息描述
    String url	// 消息链接

    @Override
    String toString() {
        "${super.toString()}:${url}"
    }
}
