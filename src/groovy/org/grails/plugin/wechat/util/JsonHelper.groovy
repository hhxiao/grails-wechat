package org.grails.plugin.wechat.util

import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.grails.plugin.wechat.WeixinException
import org.grails.plugin.wechat.bean.ReturnCode

/**
 * Created by hhxiao on 9/29/14.
 */
class JsonHelper {
    static Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create()

    static <T> T parseJson(String text, Class<T> targetClass) throws WeixinException {
        if(text.contains("errcode")) {
            ReturnCode error = gson.fromJson(text, ReturnCode.class)
            throw new WeixinException(error)
        }
        T t = gson.fromJson(text, targetClass)
        t.metaClass.toJson = { text }
        return t
    }

    static String toJson(Object obj) {
        gson.toJson(obj)
    }
}
