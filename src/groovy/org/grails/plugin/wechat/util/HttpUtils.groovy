package org.grails.plugin.wechat.util
/**
 * Created by haihxiao on 2014/9/29.
 */
class HttpUtils {

    public static final String CONTENT_TYPE_JSON = 'application/json'

    static String get(String url) {
        new URL(url).text
    }

    static String postJson(String url, String content) {
        post(url, CONTENT_TYPE_JSON, content)
    }

    static String post(String url, String contentType, String content) {
        HttpURLConnection conn = (HttpURLConnection)new URL(url).openConnection()
        conn.setRequestProperty("Content-Type", contentType)
        conn.setRequestMethod("POST")
        conn.setDoOutput(true)
        OutputStreamWriter out = new OutputStreamWriter(conn.outputStream);
        out.write(content)
        out.flush()
        out.close()
        return conn.inputStream.text
    }
}
