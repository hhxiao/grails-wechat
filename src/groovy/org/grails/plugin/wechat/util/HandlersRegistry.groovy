package org.grails.plugin.wechat.util

import org.grails.plugin.wechat.annotation.MessageHandler
import org.grails.plugin.wechat.annotation.PaymentHandler
import org.grails.plugin.wechat.bean.PayData
import org.grails.plugin.wechat.message.*
import org.springframework.util.ReflectionUtils

import java.lang.reflect.Method

/**
 * Created by haihxiao on 2014/9/30.
 */
class HandlersRegistry {
    def grailsApplication

    class MHandler {
        Collection<MsgType> msgTypes
        Collection<EventType> eventTypes
        Collection<String> eventKeys
        Method method
        Class serviceClass
        Object serviceInstance
        int priority

        MHandler(Collection<MsgType> msgTypes, Collection<EventType> eventTypes, Collection<String> eventKeys, int priority, Method method, Class serviceClass) {
            this.msgTypes = msgTypes
            this.eventTypes = eventTypes
            this.eventKeys = eventKeys
            this.method = method
            this.serviceClass = serviceClass
            this.priority = priority
        }

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
            (ResponseMessage)method.invoke(serviceInstance, message)
        }
    }

    class PHandler {
        Method method
        Class serviceClass
        Object serviceInstance
        int priority

        PHandler(int priority, Method method, Class serviceClass) {
            this.method = method
            this.serviceClass = serviceClass
            this.priority = priority
        }

        boolean applied(PayData payData) {
            return true
        }

        PayData process(PayData payData) {
            if(serviceInstance == null) {
                serviceInstance = grailsApplication.mainContext.getBean(serviceClass)
            }
            (PayData)method.invoke(serviceInstance, payData)
        }
    }

    private Map<Class, List<MHandler>> messageHandlers = new HashMap<>()
    private Map<Class, List<PHandler>> paymentHandlers = new HashMap<>()

    void reloadHandlers() {
        unregisterHandlers([])
        def classes = grailsApplication.serviceClasses*.clazz.findAll { Class clazz ->
            return !clazz.getPackage() || !clazz.getPackage().getName().startsWith("org.grails.plugin.wechat")
        }
        registerHandlers(classes)
    }

    void reloadHandler(Class serviceClass) {
        unregisterHandlers([serviceClass])
        registerHandlers([serviceClass])
    }

    void registerHandlers(Collection<Class> serviceClasses) {
        serviceClasses.each {
            List<MHandler> handlers = findMessageHandlers(it)
            if(handlers) {
                this.messageHandlers.put(it, handlers)
            }
            List<PHandler> paymentHandlers = findPaymentHandlers(it)
            if(paymentHandlers) {
                this.paymentHandlers.put(it, paymentHandlers)
            }
        }
    }

    void unregisterHandlers(Collection<Class> serviceClasses) {
        serviceClasses.findAll{!it.getPackage() || !it.getPackage().getName().startsWith("org.grails.plugin.wechat")}.each {
            messageHandlers.remove(it)
            paymentHandlers.remove(it)
        }
    }

    private List<MHandler> findMessageHandlers(Class serviceClass) {
        List<MHandler> handlers = new ArrayList<>()
        ReflectionUtils.doWithMethods(serviceClass, new ReflectionUtils.MethodCallback() {
            @Override
            void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
                findMessageHandler(serviceClass, method, handlers)
            }
        })
        handlers
    }

    private List<PHandler> findPaymentHandlers(Class serviceClass) {
        List<PHandler> handlers = new ArrayList<>()
        ReflectionUtils.doWithMethods(serviceClass, new ReflectionUtils.MethodCallback() {
            @Override
            void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
                findPaymentHandler(serviceClass, method, handlers)
            }
        })
        handlers
    }

    private void findMessageHandler(Class serviceClass, Method method, List<MHandler> handlers) {
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
            handlers.add(new MHandler(msgTypes, eventTypes, eventKeys, 0, method, serviceClass))
        } else {
            Collection<MsgType> msgTypes = messageHandler.value().toList()
            Collection<EventType> eventTypes = messageHandler.events().toList()
            Collection<String> eventKeys = messageHandler.keys().toList()
            if(msgTypes.contains(MsgType.event) && eventTypes.empty) eventTypes = EventType.values()
            handlers.add(new MHandler(msgTypes, eventTypes, eventKeys, messageHandler.priority(), method, serviceClass))
        }
    }

    private void findPaymentHandler(Class serviceClass, Method method, List<PHandler> handlers) {
        if(PayData.class != method.getReturnType()) return
        if(method.getParameterTypes().length != 1) return
        if(PayData.class != method.getParameterTypes()[0]) return

        PaymentHandler paymentHandler = method.getAnnotation(PaymentHandler.class)
        if(paymentHandler && paymentHandler.exclude()) return
        if(paymentHandler) {
            handlers.add(new PHandler(paymentHandler.priority(), method, serviceClass))
        }
    }

    Collection<MHandler> getMessageHandlers(Message message) {
        List<MHandler> handlers = new ArrayList<>()
        this.messageHandlers.each { serviceClass, hs ->
            hs.each { handler ->
                if(handler.applied(message)) handlers << handler
            }
        }
        Collections.sort(handlers, new Comparator<MHandler>() {
            @Override
            int compare(MHandler o1, MHandler o2) {
                int res = o1.priority - o2.priority
                if(res == 0) {
                    return o1.eventTypes.size() - o2.eventTypes.size()
                }
                if(res == 0) {
                    return o1.eventKeys.size() - o2.eventKeys.size()
                }
                return res
            }
        })
        return handlers
    }

    Collection<PHandler> getPaymentHandlers(PayData payData) {
        List<PHandler> handlers = new ArrayList<>()
        this.paymentHandlers.each { serviceClass, hs ->
            hs.each { handler ->
                if(handler.applied(payData)) handlers << handler
            }
        }
        Collections.sort(handlers, new Comparator<PHandler>() {
            @Override
            int compare(PHandler o1, PHandler o2) {
                return o1.priority - o2.priority
            }
        })
        return handlers
    }
}
