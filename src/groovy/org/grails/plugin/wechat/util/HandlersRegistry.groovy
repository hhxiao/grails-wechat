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
        Collection<MsgType> msgTypes
        Collection<EventType> eventTypes
        Collection<String> eventKeys
        Method method
        Class serviceClass
        Object serviceInstance
        int priority

        Handler(Collection<MsgType> msgTypes, Collection<EventType> eventTypes, Collection<String> eventKeys, int priority, Method method, Class serviceClass) {
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

    class PaymentHandler {
        Method method
        Class serviceClass
        Object serviceInstance
        int priority

        PaymentHandler(int priority, Method method, Class serviceClass) {
            this.method = method
            this.serviceClass = serviceClass
            this.priority = priority
        }

        boolean applied(String openId, String productId) {
            return true
        }

        String process(String openId, String productId) {
            if(serviceInstance == null) {
                serviceInstance = grailsApplication.mainContext.getBean(serviceClass)
            }
            (String)method.invoke(serviceInstance, openId, productId)
        }
    }

    private Map<Class, List<Handler>> messageHandlers = new HashMap<>()
    private Map<Class, List<PaymentHandler>> paymentHandlers = new HashMap<>()

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
            List<Handler> handlers = findMessageHandlers(it)
            if(handlers) {
                this.messageHandlers.put(it, handlers)
            }
            List<PaymentHandler> paymentHandlers = findPaymentHandlers(it)
            if(paymentHandlers) {
                this.paymentHandlers.put(it, paymentHandlers)
            }
        }
    }

    void unregisterHandlers(Collection<Class> serviceClasses) {
        serviceClasses.each {
            messageHandlers.remove(it)
            paymentHandlers.remove(it)
        }
    }

    private List<Handler> findMessageHandlers(Class serviceClass) {
        List<Handler> handlers = new ArrayList<>()
        ReflectionUtils.doWithMethods(serviceClass, new ReflectionUtils.MethodCallback() {
            @Override
            void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
                findMessageHandler(serviceClass, method, handlers)
            }
        })
        handlers
    }

    private List<PaymentHandler> findPaymentHandlers(Class serviceClass) {
        List<PaymentHandler> handlers = new ArrayList<>()
        ReflectionUtils.doWithMethods(serviceClass, new ReflectionUtils.MethodCallback() {
            @Override
            void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
                findPaymentHandler(serviceClass, method, handlers)
            }
        })
        handlers
    }

    private void findMessageHandler(Class serviceClass, Method method, List<Handler> handlers) {
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
            handlers.add(new Handler(msgTypes, eventTypes, eventKeys, 0, method, serviceClass))
        } else {
            Collection<MsgType> msgTypes = messageHandler.value().toList()
            Collection<EventType> eventTypes = messageHandler.events().toList()
            Collection<String> eventKeys = messageHandler.keys().toList()
            if(msgTypes.contains(MsgType.event) && eventTypes.empty) eventTypes = EventType.values()
            handlers.add(new Handler(msgTypes, eventTypes, eventKeys, messageHandler.priority(), method, serviceClass))
        }
    }

    private void findPaymentHandler(Class serviceClass, Method method, List<PaymentHandler> handlers) {
        if(String.class != method.getReturnType()) return
        if(method.getParameterTypes().length != 2) return
        if(String.class != method.getParameterTypes()[0]) return
        if(String.class != method.getParameterTypes()[1]) return

        org.grails.plugin.wechat.annotation.PaymentHandler paymentHandler = method.getAnnotation(org.grails.plugin.wechat.annotation.PaymentHandler.class)
        if(paymentHandler && paymentHandler.exclude()) return
        if(paymentHandler) {
            handlers.add(new PaymentHandler(paymentHandler.priority(), method, serviceClass))
        }
    }

    Collection<Handler> getMessageHandlers(Message message) {
        List<Handler> handlers = new ArrayList<>()
        this.messageHandlers.each { serviceClass, hs ->
            hs.each { handler ->
                if(handler.applied(message)) handlers << handler
            }
        }
        Collections.sort(handlers, new Comparator<Handler>() {
            @Override
            int compare(Handler o1, Handler o2) {
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

    Collection<PaymentHandler> getPaymentHandlers(String openId, String productId) {
        List<PaymentHandler> handlers = new ArrayList<>()
        this.paymentHandlers.each { serviceClass, hs ->
            hs.each { handler ->
                if(handler.applied(openId, productId)) handlers << handler
            }
        }
        Collections.sort(handlers, new Comparator<PaymentHandler>() {
            @Override
            int compare(PaymentHandler o1, PaymentHandler o2) {
                return o1.priority - o2.priority
            }
        })
        return handlers
    }
}
