package org.grails.plugin.wechat.message

import groovy.util.slurpersupport.GPathResult

/**
 * Created by haihxiao on 2014/12/11.
 */
class SendPicsInfo implements XmlSerializable {
    int count
    List<Item> picList = new ArrayList<>()

    @Override
    void serialize(GPathResult node) {
        count = node.Count.text().toInteger()
        node.PicList.each {
            picList << new Item(picMd5Sum: it.text())
        }
    }

    @Override
    String toString() {
        picList.collect{it.picMd5Sum}
    }

    static class Item {
        String picMd5Sum
    }
}
