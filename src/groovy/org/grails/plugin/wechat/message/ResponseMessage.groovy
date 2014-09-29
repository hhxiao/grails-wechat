package org.grails.plugin.wechat.message

import org.grails.plugin.wechat.util.JsonHelper

/**
 * Created by haihxiao on 2014/9/29.
 */
interface ResponseMessage {
    String getAdditionalResponseXml()
    Map<String, Object> getAdditionalResponseJson()
}
