package org.grails.plugin.wechat.util

import org.grails.plugin.wechat.annotation.MessageHandler
import org.grails.plugin.wechat.message.*
import org.springframework.util.ReflectionUtils

import java.lang.reflect.Method

/**
 * Created by haihxiao on 2014/9/30.
 */
class HandlersRegistry {
    def grailsApplication

    class Handler {
        Handler(Collection<MsgType> msgTypes, Collection<EventType> eventTypes, Collection<String> eventKeys, Method method, Class serviceClass) {
            this.msgTypes = msgTypes
            this.eventTypes = eventTypes
            this.eventKeys = eventKeys
            this.method = method
            this.serviceClass = serviceClass
        }

        Collection<MsgType> msgTypes
        Collection<EventType> eventTypes
        Collection<String> eventKeys
        Method method
        Class serviceClass
        Object serviceInstance

        boolean applied(Message message) {
            if(msgTypes.contains(message.msgType)) {
                if(message.msgType == MsgType.event) {
                    EventMessage eventMessage = (EventMessage)message
                    if(eventTypes.contains(eventMessage.event)) {
                        return (eventKeys.empty || eventKeys.contains(eventMessage.eventKey))
                    }
                    return false
                }
                if(message.msgType == MsgType.event && eventTypes.contains(((EventMessage)message).event)) {
                    return true
                } else if(message.msgType != MsgType.event) {
                    return true
                }
            }
            return false
        }

        ResponseMessage process(Message message) {
            if(serviceInstance == null) {
                serviceInstance = grailsApplication.mainContext.getBean(serviceClass)
            }
            method.invoke(serviceInstance, message)
        }
    }

    private Map<Class, List<Handler>> serviceHandlers = new HashMap<>()

    void reloadHandlers() {
        unregisterHandlers([])
        registerHandlers(grailsApplication.serviceClasses*.clazz)
    }

    void reloadHandler(Class serviceClass) {
        unregisterHandlers([serviceClass])
        registerHandlers([serviceClass])
    }

    void registerHandlers(Collection<Class> serviceClasses) {
        serviceClasses.each {
            List<Handler> handlers = getServiceHandlers(it)
            if(handlers) {
                serviceHandlers.put(it, handlers)
            }
        }
    }

    void unregisterHandlers(Collection<Class> serviceClasses) {
        serviceClasses.each {
            serviceHandlers.remove(it)
        }
    }

    private List<Handler> getServiceHandlers(Class serviceClass) {
        List<Handler> handlers = new ArrayList<>()
        ReflectionUtils.doWithMethods(serviceClass, new ReflectionUtils.MethodCallback() {
            @Override
            void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
                findHandler(serviceClass, method, handlers)
            }
        })
        handlers
    }

    private void findHandler(Class serviceClass, Method method, List<Handler> handlers) {
        if(!ResponseMessage.class.isAssignableFrom(method.getReturnType())) return
        if(method.getParameterTypes().length != 1) return

        if(!Message.class.isAssignableFrom(method.getParameterTypes()[0])) return

        MessageHandler messageHandler = method.getAnnotation(MessageHandler.class)
        if(messageHandler && messageHandler.exclude()) return

        if(messageHandler == null) {
            Collection<MsgType> msgTypes = MessageUtils.getApplicableMsgTypes(method.getParameterTypes()[0])
            Collection<EventType> eventTypes = []
            Collection<String> eventKeys = []
            if(msgTypes.contains(MsgType.event)) eventTypes = EventType.values()
            handlers.add(new Handler(msgTypes, eventTypes, eventKeys, method, serviceClass))
        } else {
            Collection<MsgType> msgTypes = messageHandler.value().toList()
            Collection<EventType> eventTypes = messageHandler.events().toList()
            Collection<String> eventKeys = messageHandler.keys().toList()
            if(msgTypes.contains(MsgType.event) && eventTypes.empty) eventTypes = EventType.values()
            handlers.add(new Handler(msgTypes, eventTypes, eventKeys, method, serviceClass))
        }
    }

    Collection<Handler> getMessageHandlers(Message message) {
        List<Handler> messageHandlers = new ArrayList<>()
        serviceHandlers.each { serviceClass, handlers ->
            handlers.each { handler ->
                if(handler.applied(message)) messageHandlers << handler
            }
        }
        Collections.sort(messageHandlers, new Comparator<Handler>() {
            @Override
            int compare(Handler o1, Handler o2) {
                return o1.eventKeys.size() - o2.eventKeys.size()
            }
        })
        return messageHandlers
    }
}
