package org.grails.plugin.wechat.util

import net.sf.ehcache.Cache
import net.sf.ehcache.CacheManager
import net.sf.ehcache.Element
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.grails.plugin.wechat.session.WechatSession

/**
 * Created by haihxiao on 2015/1/10.
 */
class WechatSessionHelper {
    private static Log log = LogFactory.getLog(WechatSessionHelper.class)

    private static final ThreadLocal<WechatSession> SESSION_HOLDER = new ThreadLocal<WechatSession>();

    private static Cache sessionCache = CacheManager.getInstance().getCache("wechat.SessionCache")

    public static void reset() {
        SESSION_HOLDER.set(null);
    }

    /**
     * Find or create a new session.
     */
    public static void set(String wechatId) {
        Element element = sessionCache.get(wechatId)
        if(element) {
            set((WechatSession)element.value)
        } else {
            if(log.isDebugEnabled()) {
                log.debug("${wechatId} initialized a session")
            }
            def session = new WechatSession(wechatId)
            sessionCache.put(new Element(wechatId, session))
            set(session)
        }
    }

    /**
     /**
     * Set the current session.
     * @param request the session
     */
    public static void set(final WechatSession session) {
        SESSION_HOLDER.set(session);
    }

    /**
     * Get the current session.
     * @return the session
     */
    public static WechatSession getCurrent() {
        return SESSION_HOLDER.get();
    }
}
