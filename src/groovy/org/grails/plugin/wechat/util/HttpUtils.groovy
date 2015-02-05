package org.grails.plugin.wechat.util

import java.nio.charset.Charset

/**
 * Created by haihxiao on 2014/9/29.
 */
class HttpUtils {

    public static final String CONTENT_TYPE_JSON = 'application/json; encoding=utf-8'

    static String get(String url) {
        new URL(url).getText('UTF-8')
    }

    static String postJson(String url, String content) {
        post(url, CONTENT_TYPE_JSON, content)
    }

    static String postJson(String url, Map content) {
        postJson(url, JsonHelper.toJson(content))
    }

    static String post(String url, String contentType, String content) {
        HttpURLConnection conn = (HttpURLConnection)new URL(url).openConnection()
        conn.setRequestProperty("Content-Type", contentType)
        conn.setRequestMethod("POST")
        conn.setDoOutput(true)
        OutputStreamWriter out = new OutputStreamWriter(conn.outputStream, "UTF-8")
        out.write(content)
        out.flush()
        out.close()
        return conn.inputStream.text
    }
}
