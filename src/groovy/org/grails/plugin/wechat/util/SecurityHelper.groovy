package org.grails.plugin.wechat.util
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.codehaus.groovy.grails.commons.GrailsApplication
/**
 * Created by haihxiao on 2014/12/9.
 */
class SecurityHelper {
    private Log log = LogFactory.getLog(SecurityHelper.class)

    private GrailsApplication application
    private Class<?> domainClass
    private String enabledPropertyName
    private String wechatField

    SecurityHelper(GrailsApplication application, String domainClassName, String enabledPropertyName, String wechatField, String injectField) {
        this.domainClass = application.getDomainClass(domainClassName).clazz
        this.enabledPropertyName = enabledPropertyName ? enabledPropertyName.capitalize() : null
        this.wechatField = wechatField.capitalize()

        this.domainClass.metaClass.static."get${injectField.capitalize()}" = {
            Wrapper.get().principal
        }
    }

    Object authenticate(String wechatId) {
        def user
        if(enabledPropertyName) {
            user = domainClass."findBy${wechatField}And${enabledPropertyName}"(wechatId, true)
        } else {
            user = domainClass."findBy${wechatField}"(wechatId)
        }
        if(user) {
            Wrapper.set(user)
            if(log.debugEnabled) log.debug("Authencitated $wechatId to ${user}")
        }
        return user
    }

    static void reset() {
        Wrapper.reset()
    }

    private static class Wrapper implements Serializable {
        private Object principal;

        public static Wrapper get() {
            Wrapper token = threadToken.get();
            if(token == null) {
                token = new Wrapper();
                threadToken.set(token);
            }
            return token;
        }

        public static Wrapper set(Object authenticated) {
            Wrapper token = threadToken.get();
            token.principal = authenticated;
            return token;
        }

        /**
         * Clear the token in the current thread
         */
        public static void reset() {
            threadToken.remove();
        }

        @Override
        public String toString() {
            return String.valueOf(principal);
        }

        private static ThreadLocal<Wrapper> threadToken = new ThreadLocal<Wrapper>(){
            public Wrapper initialValue(){
                return new Wrapper();
            }
        };
    }
}
