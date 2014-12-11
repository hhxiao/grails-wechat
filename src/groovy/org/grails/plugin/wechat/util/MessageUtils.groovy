package org.grails.plugin.wechat.util

import groovy.util.slurpersupport.GPathResult
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.grails.plugin.wechat.message.EventType
import org.grails.plugin.wechat.message.Message
import org.grails.plugin.wechat.message.MsgType
import org.grails.plugin.wechat.message.ResponseMessage
import org.grails.plugin.wechat.message.XmlSerializable

import java.lang.reflect.Method

/**
 * Created by haihxiao on 2014/9/29.
 */
class MessageUtils {
    private static final Log log = LogFactory.getLog(MessageUtils.class)

    static String toXml(ResponseMessage message) {
        """<xml>
 <ToUserName><![CDATA[${message.toUserName}]]></ToUserName>
 <FromUserName><![CDATA[${message.fromUserName}]]></FromUserName>
 <CreateTime>${message.createTime}</CreateTime>
 <MsgType><![CDATA[${message.msgType.name()}]]></MsgType>
 ${message.getAdditionalResponseXml()}
</xml>
"""
    }

    static String toJson(ResponseMessage message) {
        """{
    "touser":"${message.toUserName}",
    "msgtype":"${message.msgType.name()}",
    "${message.msgType.name()}": ${JsonHelper.toJson(message.getAdditionalResponseJson())}
}
"""
    }

    static Message fromGPathResult(GPathResult xml) {
        String type = xml.MsgType
        Message message = (Message)Thread.currentThread().getContextClassLoader().loadClass("${Message.package.name}.${type.capitalize()}Message").newInstance()

        convert(xml.children().findAll {it.name() != 'MsgType'}, message)
        return message
    }

    private static Object convert(GPathResult nodes, Object target) {
        nodes.each {
            String name = it.name()
            try {
                Method getter = target.class.getMethod("get$name")
                Class<?> returnType = getter.getReturnType()
                Method setter = target.class.getMethod("set$name", returnType)
                Object ret = convert(it, returnType)
                if(ret != null) setter.invoke(target, ret)
            } catch(NoSuchMethodException e) {}
        }
        return target
    }

    private static Object convert(GPathResult node, Class<?> returnType) {
        switch (returnType) {
            case String.class:
                return node.text().trim()
            case long.class:
            case Long.class:
                return node.text().trim().toLong()
            case int.class:
            case Integer.class:
                return node.text().trim().toInteger()
            case boolean.class:
            case Boolean.class:
                return Boolean.valueOf(node.text().trim())
            case EventType.class:
                return EventType.of(node.text().trim())
            default:
                if(returnType.isEnum()) {
                    return Enum.valueOf(returnType, node.text().trim())
                }
                if(returnType.getPackage() == Message.class.getPackage()) {
                    Object instance = returnType.getConstructor().newInstance()
                    if(instance instanceof XmlSerializable) {
                        instance.serialize(node)
                        return instance
                    } else {
                        return convert(node.children(), instance)
                    }
                }
        }
        log.warn("unknown attribute: ${node.name()}")
        return null
    }

    static Message fromXml(String text) {
        XmlSlurper slurper = new XmlSlurper()
        return fromGPathResult(slurper.parseText(text))
    }

    static Collection<MsgType> getApplicableMsgTypes(Class<? extends Message> messageClass) {
        if(messageClass == Message.class) return MsgType.values()
        String simpleName = messageClass.simpleName
        MsgType msgType = MsgType.valueOf(simpleName.substring(0, simpleName.length() - 'message'.length()).toLowerCase())
        return [msgType]
    }
}
