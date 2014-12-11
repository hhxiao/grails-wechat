package org.grails.plugin.wechat.message

import groovy.util.slurpersupport.GPathResult

/**
 * Created by haihxiao on 2014/12/11.
 */
interface XmlSerializable {
    void serialize(GPathResult node)
}
