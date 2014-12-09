package org.grails.plugin.wechat.message

/**
 * Created by hhxiao on 9/29/14.
 */
class NewsMessage extends Message implements ResponseMessage {
    List<Article> articles = []

    String getAdditionalResponseXml() {
        if(articles) {
            """<ArticleCount>${articles.size()}</ArticleCount>
 <Articles>
${articles.collect{it.toXml()}.join('')} </Articles>"""
        } else {
            """<ArticleCount>${articles.size()}</ArticleCount>"""
        }
    }

    Map<String, Object> getAdditionalResponseJson() {
        ['articles': articles.collect{ it.toJson() }]
    }

    @Override
    String toString() {
        "${super.toString()}:${articles.collect {it.title}}"
    }
}
