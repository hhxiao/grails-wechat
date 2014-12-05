package org.grails.plugin.wechat.message

import java.lang.reflect.Method
/**
 * Created by hhxiao on 9/29/14.
 */
abstract class Message {
    Message() {
        String simpleName = getClass().simpleName
        msgType = MsgType.valueOf(simpleName.substring(0, simpleName.length() - 'message'.length()).toLowerCase())
        createTime = System.currentTimeMillis() / 1000
    }

    /**
     * 消息id，64位整型
     */
    long msgId

    /**
     * 消息类型 text、image、location、link
     */
    MsgType msgType

    /**
     * 接收方微信号（OpenID）
     */
    String toUserName
    /**
     * 发送方微信号（一个OpenID）
     */
    String fromUserName
    /**
     * 消息创建时间 （整型）
     */
    long createTime

    static Message fromXml(String text) {
        XmlSlurper slurper = new XmlSlurper()
        def xml = slurper.parseText(text)
        String type = xml.MsgType
        MsgType msgType = MsgType.valueOf(type)
        Message message = (Message)Thread.currentThread().getContextClassLoader().loadClass("${Message.package.name}.${type.capitalize()}Message").newInstance()
        message.msgType = msgType

        message.class.methods.toList().each { println it.name }
        xml.children().findAll {it.name() != 'MsgType'}.each {
            String name = it.name()
            Method getter = message.class.getMethod("get$name")
            Method setter = message.class.getMethod("set$name", getter.getReturnType())
            def value = it.text().trim()
            if(getter.getReturnType() == long.class) {
                setter.invoke(message, value.toLong())
            } else if(getter.getReturnType().isEnum()) {
                setter.invoke(message, Enum.valueOf(getter.getReturnType(), value))
            } else {
                setter.invoke(message, value)
            }
        }
        return message
    }
}
