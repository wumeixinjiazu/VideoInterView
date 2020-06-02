package com.videocomm.VideoInterView.bean;

/**
 * @author[wengCJ]
 * @version[创建日期，2020/5/27 0027]
 * @function[功能简介 聊天消息]
 **/
public class ChatMsg {
    public static final int RECEIVED = 0;//收到一条消息

    public static final int SENT = 1;//发出一条消息

    private String content;//消息的内容

    private int type;//消息的类型

    private String user;//发送的用户
    private String time;

    public String getUser() {
        return user;
    }

    public ChatMsg(String content, String user, int type, String time) {
        this.content = content;
        this.type = type;
        this.user = user;
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public int getType() {
        return type;
    }

    public String getTime() {
        return time;
    }
}
