package com.videocomm.VideoInterView.bean;

import java.util.List;

/**
 * @author[wengCJ]
 * @version[创建日期，2020/4/29 0029]
 * @function[功能简介]
 **/
public class QueueBean {

    private List<QueueListBean> queue_list;

    public List<QueueListBean> getQueue_list() {
        return queue_list;
    }

    public void setQueue_list(List<QueueListBean> queue_list) {
        this.queue_list = queue_list;
    }

    public static class QueueListBean {
        /**
         * describe : 开户队列描述信息
         * name : 开户队列
         * property : 0
         * queueid : 100
         */

        private String describe;
        private String name;
        private int property;
        private String queueid;

        public String getDescribe() {
            return describe;
        }

        public void setDescribe(String describe) {
            this.describe = describe;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getProperty() {
            return property;
        }

        public void setProperty(int property) {
            this.property = property;
        }

        public String getQueueid() {
            return queueid;
        }

        public void setQueueid(String queueid) {
            this.queueid = queueid;
        }
    }
}
