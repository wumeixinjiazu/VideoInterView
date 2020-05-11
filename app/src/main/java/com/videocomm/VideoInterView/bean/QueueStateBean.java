package com.videocomm.VideoInterView.bean;

/**
 * @author[wengCJ]
 * @version[创建日期，2020/4/29 0029]
 * @function[功能简介]
 **/
public class QueueStateBean {

    /**
     * {
     * "index": 2,
     * "length": 10,
     * "time": 50
     * }
     * <p>
     * JSON字段说明：
     * index，当前排队在第几位，比如上图排在第2位
     * length，当前队列一共有几人排队，比如上图一共有10人
     * time，当前排队等待时长，单位秒
     */

    private int index;
    private int length;
    private int time;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }
}
